
% 1. No mirrors = no change in direction
path(east, [X, Y], Obstacles, Person_X, Mirrors, H):-
    \+blocked([X, Y], Obstacles, Person_X), % Check if the square is blocked
    Right is X + 1, % Calculate next position of the laser
    path(east, [Right, Y], Obstacles, Person_X, Mirrors, H). % Continue to the next square

% 2. \ mirror
path(east, [X, Y], Obstacles, Person_X, [Mirror | L], H):-
    \+blocked([X, Y], Obstacles, Person_X),
    Down is Y + 1,
    [X, Y, /] = Mirror,
    path(south, [X, Down], Obstacles, Person_X, L, H).

% 3. / mirror
path(east, [X, Y], Obstacles, Person_X, [Mirror | L], H):-
    \+blocked([X, Y], Obstacles, Person_X),
    Up is Y - 1,
    [X, Y, \] = Mirror,
    path(north, [X, Up], Obstacles, Person_X, L, H).

% North

path(north, [X, Y], Obstacles, Person_X, Mirrors, H):-
    \+blocked([X, Y], Obstacles, Person_X),
    Up is Y - 1,
    path(north, [X, Up], Obstacles, Person_X, Mirrors, H).

% Not including the mirror to reflect towards west because our goal is to the east

path(north, [X, Y], Obstacles, Person_X, [Mirror | L], H):-
    \+blocked([X, Y], Obstacles, Person_X),
    Right is X + 1,
    [X, Y, \] = Mirror,
    path(east, [Right, Y], Obstacles, Person_X, L, H).

% South

path(south, [X, Y], Obstacles, Person_X, Mirrors, H):-
    \+blocked([X, Y], Obstacles, Person_X),
    Down is Y + 1,
    path(south, [X, Down], Obstacles, Person_X, Mirrors, H).

path(south, [X, Y], Obstacles, Person_X, [Mirror | L], H):-
    \+blocked([X, Y], Obstacles, Person_X),
    Right is X + 1,
    [X, Y, /] = Mirror,
    path(east, [Right, Y], Obstacles, Person_X, L, H).

% Not including the mirror to reflect towards west because our goal is to the east

% Checking if the person fits the gap

checkPersonBounds(_, []):- true.

checkPersonBounds(Person_X, [O | L]):-
    [O_X, O_Width, O_Height] = O, % Get obstacle props
    (
       Person_X >= O_X+O_Width;
       Person_X < O_X; % Check if the person overlaps with the obstacle
       O_Height < 5
     ),
     checkPersonBounds(Person_X, L). % Check the next obstacle

% Placing the person at x position 1 to 10
% For x = 0 or x = 11, the laser source and the goal will be blocked
% so we skip them.

placePerson(Person_X, Obstacles, Mirrors, H):-
    checkPersonBounds(Person_X, Obstacles), % Check if the person fits
    path(east, [2,H], Obstacles, Person_X, Mirrors, H); % Create a laser path starting from the square next to the source
    Person_X1 is Person_X + 1, % Calculate next x position
    Person_X1 < 11, % Check if the person is still inside the bounds
    placePerson(Person_X1, Obstacles, Mirrors, H). % Try with the person in the next square


% Place N mirrors by making sure Mirrors has a length of N

placeNMirrors(H, Obstacles, Mirrors, N):-
    length(Mirrors, N),
    placePerson(1, Obstacles, Mirrors, H).

% The possible mirror counts are 0, 4, 6 and 8. We check them seperately

placeMirrors(H, Obstacles, Mirrors):-
    placeNMirrors(H, Obstacles, Mirrors, 0);
    placeNMirrors(H, Obstacles, Mirrors, 4);
    placeNMirrors(H, Obstacles, Mirrors, 6);
    placeNMirrors(H, Obstacles, Mirrors, 8).

