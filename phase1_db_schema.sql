---DROP ALL TABLES TO MAKE SURE THE SCHEMA IS CLEAR
DROP TABLE profile CASCADE CONSTRAINTS;
DROP TABLE friends CASCADE CONSTRAINTS;
DROP TABLE pendingFriends CASCADE CONSTRAINTS;
DROP TABLE messages CASCADE CONSTRAINTS;
DROP TABLE messageRecipient CASCADE CONSTRAINTS;
DROP TABLE groups CASCADE CONSTRAINTS;
DROP TABLE groupMembership CASCADE CONSTRAINTS;
DROP TABLE pendingGroupmembers CASCADE CONSTRAINTS;

--Stores the profile and login information for each user registered in the system
CREATE TABLE profile(
  userID varchar2(20) NOT NULL,
  name varchar2(50),
  password varchar2(50),
  date_of_birth date,
  lastlogin timestamp,
  CONSTRAINT profile_PK PRIMARY KEY (userID)
);

--Stores the friends lists for every user in the system. The JDate is when they became friends,
--and the message is the message of friend request
CREATE TABLE friends(
  userID1 varchar2(20) NOT NULL,
  userID2 varchar2(20) NOT NULL,
  JDate date,
  message varchar2(200),
  CONSTRAINT friends_PK PRIMARY KEY (userID1,userID2)
);

--Stores pending friends requests that have yet to be confirmed by the recipient of the request.
CREATE TABLE pendingFriends(
  fromID varchar2(20) NOT NULL,
  toID varchar2(20) NOT NULL,
  message varchar2(200)
  CONSTRAINT pendingFriends_PK PRIMARY KEY (fromID,toID)
);

--Stores every message sent by users in the system. Note that the default values of ToUserID
--and ToGroupID should be NULL
CREATE TABLE messages(
  msgID varchar2(20) NOT NULL,
  fromID varchar2(20) NOT NULL,
  message varchar2(200),
  toUserID varchar2(20) DEFAULT NULL,
  toGroupID varchar2(20) DEFAULT NULL,
  dateSent date,
  CONSTRAINT messages_PK PRIMARY KEY (msgID),
  CONSTRAINT messages_FK FOREIGN KEY (fromID) REFERENCES profile(userID)
);

--Stores the recipients of each message stored in the system
CREATE TABLE messageRecipient(
  msgID varchar2(20) NOT NULL,
  userID varchar2(20) NOT NULL,
  CONSTRAINT messageRecipient_PK PRIMARY KEY (msgID),
  CONSTRAINT messageRecipient_FK1 FOREIGN KEY (msgID) REFERENCES messages(msgID),
  CONSTRAINT messageRecipient_FK2 FOREIGN KEY (userID) REFERENCES profile(userID)
);

--Stores information for each group in the system
CREATE TABLE groups(
  gID varchar2(20) NOT NULL,
  name varchar2(50),
  description varchar2(200),
  CONSTRAINT groups_PK PRIMARY KEY (gID)
);

--Stores the users who are members of each group in the system.The ’role’ indicate whether a
--user is a manager of a group (who can accept joining group request) or not.
CREATE TABLE groupMembership(
  gID varchar2(20) NOT NULL,
  userID varchar2(20) NOT NULL,
  role varchar2(20),
  CONSTRAINT groupMembership_PK PRIMARY KEY (gID),
  CONSTRAINT groupMembership_FK1 FOREIGN KEY (gID) REFERENCES groups(gID),
  CONSTRAINT groupMembership_FK2 FOREIGN KEY (userID) REFERENCES profile(userID)
);

--Stores pending joining group requests that have yet to be accept/reject by the manager of the
--group.
CREATE TABLE pendingGroupmembers(
  gID varchar2(20) NOT NULL,
  userID varchar2(20) NOT NULL,
  message varchar2(200),
  CONSTRAINT pendingGroupmembers_PK PRIMARY KEY (gID),
  CONSTRAINT pendingGroupmembers_FK1 FOREIGN KEY (gID) REFERENCES groups(gID),
  CONSTRAINT pendingGroupmembers_FK2 FOREIGN KEY (userID) REFERENCES profile(userID)
);
