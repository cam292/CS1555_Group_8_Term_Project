# CS1555_Group_8_Term_Project
CS1555 Term Project for group 8

---PART 2 IMPLEMENTATION NOTES

createUser: currently just takes password as a parameter. For the driver program in part 3, the user can enter their password there.
			the profileIndex is determined from the MAX profileIndex + 1

Login: id and name are just stored as parts of the program after you log in which will be used by other functions

ConfirmFriendship: accounts for both friends and group requests

DisplayFriends: I made it so that you can see the names of friends' friends, but you can only view a FRIEND'S profile - because if you're not their friend, you shouldn't be able to see personal things like email.

CreateGroup: groupIndex comes from MAX gid + 1 from existing group table

InitiateAddingGroup: the space left in the group is calculated from the inherent group limit - count of users in group

SearchForUser: just searches for each term in the input delimited by spaces

ThreeDegrees: starting from user A, creates all permutations of possible paths (where the next user in the path has to be a friend of the previous user). After all permutations are created, searches them for where the last user is B. If the last user is B, a match is found

Logout: Stores the timestamp and removes name and id stored in program
