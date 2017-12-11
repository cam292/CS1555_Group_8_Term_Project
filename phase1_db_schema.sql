--Drop tables initially
DROP TABLE friends CASCADE CONSTRAINTS;
DROP TABLE pendingFriends CASCADE CONSTRAINTS;
DROP TABLE messageRecipient CASCADE CONSTRAINTS;
DROP TABLE groupMembership CASCADE CONSTRAINTS;
DROP TABLE pendingGroupmembers CASCADE CONSTRAINTS;
DROP TABLE groups CASCADE CONSTRAINTS;
DROP TABLE messages CASCADE CONSTRAINTS;
DROP TABLE profile CASCADE CONSTRAINTS;

--Stores the profile and login information for each user registered in the system
CREATE TABLE profile(
  userID varchar2(20) NOT NULL NOT DEFERRABLE,
  name varchar2(50),
  email varchar2(50),
  password varchar2(50),
  date_of_birth date,
  lastlogin timestamp,
  CONSTRAINT profile_PK PRIMARY KEY (userID) INITIALLY IMMEDIATE -- new profile can't be created with an already existing ID and userIDs shouldn't change
);

--Stores the friends lists for every user in the system. The JDate is when they became friends,
--and the message is the message of friend request
CREATE TABLE friends(
  userID1 varchar2(20) NOT NULL NOT DEFERRABLE,
  userID2 varchar2(20) NOT NULL NOT DEFERRABLE,
  JDate date,
  message varchar2(200),
  CONSTRAINT friends_PK PRIMARY KEY (userID1, userID2) INITIALLY IMMEDIATE, --should only be one relation between 2 ID's
  CONSTRAINT friends_FK1 FOREIGN KEY (userID1) REFERENCES profile(userID) INITIALLY IMMEDIATE, --the user id should reference a profile in order to create a friendship
  CONSTRAINT friends_FK2 FOREIGN KEY (userID2) REFERENCES profile(userID) INITIALLY IMMEDIATE -- same as friends_FK1
);

--Stores pending friends requests that have yet to be confirmed by the recipient of the request.
CREATE TABLE pendingFriends(
  fromID varchar2(20) NOT NULL NOT DEFERRABLE,
  toID varchar2(20) NOT NULL NOT DEFERRABLE,
  message varchar2(200),
  CONSTRAINT pendingFriends_PK PRIMARY KEY (fromID,toID) INITIALLY IMMEDIATE, --both users should exist in order to create a friendship request
  CONSTRAINT pendingFriends_FK1 FOREIGN KEY (fromID) REFERENCES profile(userID) INITIALLY IMMEDIATE, --the user id should reference a profile in order to create a friend request
  CONSTRAINT pendingFriends_FK2 FOREIGN KEY (toID) REFERENCES profile(userID) INITIALLY IMMEDIATE --same as pendingFriends_FK1
);

--Stores every message sent by users in the system. Note that the default values of ToUserID
--and ToGroupID should be NULL
CREATE TABLE messages(
  msgID varchar2(20) NOT NULL,
  fromID varchar2(20) NOT NULL NOT DEFERRABLE,
  message varchar2(200),
  toUserID varchar2(20) DEFAULT NULL,
  toGroupID varchar2(20) DEFAULT NULL,
  dateSent date,
  CONSTRAINT messages_PK PRIMARY KEY (msgID) INITIALLY IMMEDIATE, --Can't duplicate msgIDs
  CONSTRAINT messages_FK FOREIGN KEY (fromID) REFERENCES profile(userID) INITIALLY IMMEDIATE --fromID should be an existing profile's userID
);

--Stores the recipients of each message stored in the system
CREATE TABLE messageRecipient(
  msgID varchar2(20) NOT NULL NOT DEFERRABLE,
  userID varchar2(20),
  CONSTRAINT messageRecipient_PK PRIMARY KEY (msgID,userID) INITIALLY IMMEDIATE, --
  CONSTRAINT messageRecipient_FK1 FOREIGN KEY (msgID) REFERENCES messages(msgID) INITIALLY IMMEDIATE, --Must be an existing msgID in messages in order to add to this table
  CONSTRAINT messageRecipient_FK2 FOREIGN KEY (userID) REFERENCES profile(userID) INITIALLY IMMEDIATE --Must be an existing profile in order to be a recipient
);

--Stores information for each group in the system
CREATE TABLE groups(
  gID varchar2(20) NOT NULL DEFERRABLE,
  memberLimit varchar2(20) NOT NULL,
  name varchar2(50) NOT NULL, --a group must have a name
  description varchar2(200),
  CONSTRAINT groups_PK PRIMARY KEY (gID) INITIALLY IMMEDIATE DEFERRABLE, --Creation of a group must be a unique ID
  CONSTRAINT groups_UK UNIQUE(name) INITIALLY IMMEDIATE DEFERRABLE --Group names should be unique
);

--Stores the users who are members of each group in the system.The ’role’ indicate whether a
--user is a manager of a group (who can accept joining group request) or not.
CREATE TABLE groupMembership(
  gID varchar2(20) NOT NULL NOT DEFERRABLE,
  userID varchar2(20) NOT NULL NOT DEFERRABLE,
  role varchar2(20),
  CONSTRAINT groupMembership_PK PRIMARY KEY (gID, userID), --Can have duplicate gIDs and userIDs, but the pairings must be unique
  CONSTRAINT groupMembership_FK1 FOREIGN KEY (gID) REFERENCES groups(gID) INITIALLY IMMEDIATE, --Must be an existing gID in groups table
  CONSTRAINT groupMembership_FK2 FOREIGN KEY (userID) REFERENCES profile(userID) INITIALLY IMMEDIATE --Must be an existing userID in profile table
);

--Stores pending joining group requests that have yet to be accept/reject by the manager of the
--group.
CREATE TABLE pendingGroupmembers(
  gID varchar2(20) NOT NULL NOT DEFERRABLE,
  userID varchar2(20) NOT NULL NOT DEFERRABLE,
  message varchar2(200),
  CONSTRAINT pendingGroupmembers_PK PRIMARY KEY (gID, userID) INITIALLY IMMEDIATE, --Can have duplicate gIDs and userIDs, but the pairings must be unique
  CONSTRAINT pendingGroupmembers_FK1 FOREIGN KEY (gID) REFERENCES groups(gID) INITIALLY IMMEDIATE, --Must be an existing gID in groups table
  CONSTRAINT pendingGroupmembers_FK2 FOREIGN KEY (userID) REFERENCES profile(userID) INITIALLY IMMEDIATE --Must be an existing userID in profile table
);

-----TRIGGERS
--After a message is sent, if it is sent to a single user (not a group), then add that user to the messageRecipient table.
CREATE OR REPLACE TRIGGER recipientsTrigger
    AFTER INSERT ON messages
    FOR EACH ROW
  BEGIN
    IF :new.toUserID IS NOT NULL THEN
      INSERT INTO messageRecipient VALUES(:new.msgID, :new.toUserID);
    END IF;
  END;
/

--After a message is sent, if it is sent to a group (not a single user), then add all users in that group to the messageRecipient table.
CREATE OR REPLACE TRIGGER recipientsGroupTrigger
AFTER INSERT ON messages
FOR EACH ROW
BEGIN
    IF :new.toGroupID IS NOT NULL THEN
    INSERT INTO messageRecipient VALUES
        (
            :new.msgID,
            (SELECT userID FROM groupMembership WHERE gID= :new.toGroupID)
        );
    END IF;
END;
/

--When a new entry is added to the friends table, first ensure that they were in pending friends and then remove from the pending friends table.
CREATE OR REPLACE TRIGGER newFriendTrigger
BEFORE INSERT ON friends
FOR EACH ROW
DECLARE
    --v_count NUMBER;
BEGIN
    --SELECT COUNT(*) INTO v_count FROM pendingFriends p WHERE :new.userID1=p.fromID AND :new.userID2=p.toID;
    --IF v_count > 0 THEN
    DELETE FROM pendingFriends p WHERE :new.userID1=p.fromID AND :new.userID2=p.toID;
    DELETE FROM pendingFriends p WHERE :new.userID2=p.fromID AND :new.userID1=p.toID;
    --ELSE
    --    ROLLBACK TRANSACTION; --this person cannot be added as they were not a pending friend
    --END IF;
END;
/

--When a new entry is added to the group members table, first ensure that they were in the pending group members table and then remove from the pending group members table
CREATE OR REPLACE TRIGGER newGroupMemberTrigger
BEFORE INSERT ON groupMembership
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
   SELECT COUNT(*) INTO v_count FROM pendingGroupmembers p WHERE :new.gID=p.gID AND :new.userID=p.userID;
   IF v_count > 0 THEN
   	DELETE FROM pendingGroupmembers p WHERE :new.gID=p.gID AND :new.userID=p.userID;
   --ELSE
   --	ROLLBACK TRANSACTION; --this person cannot be added as they were not a pending group member
   END IF;
END;
/

--When a user is deleted from profiles, delete them from all groups and
--delete messages where both the sender and reciever are deleted
CREATE OR REPLACE TRIGGER dropUser
BEFORE DELETE ON profile
FOR EACH ROW
BEGIN
  DELETE FROM pendingGroupmembers p WHERE p.userID = :old.userID;
  DELETE FROM groupMembership g WHERE g.userID = :old.userID;

  DELETE FROM friends f WHERE f.userID1 = :old.userID OR f.userID2 = :old.userID;
  DELETE FROM pendingFriends s WHERE s.fromID = :old.userID OR s.toID = :old.userID;

  DELETE FROM messages WHERE msgID = (SELECT msgID FROM messages m WHERE m.fromID=:old.userID AND m.toUserID IS NOT NULL AND m.toUserID NOT IN (SELECT userID from profile));
  DELETE FROM messages WHERE msgID = (SELECT msgID FROM messages m JOIN groupMembership g ON m.toGroupID=g.gID WHERE m.fromID=:old.userID AND (SELECT COUNT(*) FROM messages m JOIN groupMembership g ON m.toGroupID=g.gID WHERE m.fromID=:old.userID)=0);
END;
/
