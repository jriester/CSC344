#include <stdio.h>
#include <stdlib.h>
#include <string.h>
//Author: James Riester
//Course: CSC344 : Programming Languages
//Professor Schlegel

struct Node {
    char data;
    struct Node* next;
    struct Node* prev;
};

enum moveDirection {
    LEFT, RIGHT
};

struct instructions {
    int currState;
    char readVal;
    char writeVal;
    char direction;
    int newState;
};
struct linkedList{
    struct instructions **array;
    int startTape,endTape;
    struct Node *head;
};

struct instructions createInst(int state, char redVal, char write, enum moveDirection d, int newstate) {
    struct instructions newInst;
    newInst.currState = state;
    newInst.readVal = redVal;
    newInst.writeVal = write;
    newInst.direction = d;
    newInst.newState = newstate;
    return newInst;
}
struct instructions getInstructions(struct instructions** array, int row, int val) {
    //finds instruction in [][] with values given            ^        ^        ^
    struct instructions *i = malloc(sizeof(struct instructions)); 

    if (array[row][val].readVal == '\0') {
        printf("Can't read instructions at %d %d", row, val);
        exit(1);
    }
    *i = array[row][val];
    return *i;
}
struct Node *populate(struct Node *noad, enum moveDirection d, char v) {
    //populates linkedList, checks bounds on either side and creates node if noad.prev/next are NULL
    struct Node *head = noad;
    struct Node *temp = malloc(sizeof(struct Node*));
    temp->data = v;
     if (d == LEFT) {
        while (noad->prev != NULL) {
            noad = noad->prev;
        }
        temp->next = noad;
        temp->prev = NULL;
        noad->prev = temp;
    }
    if (d == RIGHT) {
        while (noad->next != NULL) {
            noad = noad->next;
        }
        temp->prev = noad;
        temp->next = NULL;
        noad->next = temp;
    }
    return head;
}

void printLinkedList(struct linkedList ll) {

     while (ll.head != NULL) {
            printf("%c ", ll.head->data);
            ll.head = ll.head->next;
          }
     printf("\n");
    }

void freeLinkedList(struct Node* head) {
    struct Node* temp;
    while(head != NULL) {
        temp = head;
        head = head->next;
        free(temp);
    }
}

struct linkedList initList(char*input) {

    struct linkedList *ll = malloc(sizeof(struct linkedList));
    FILE *fPointer = fopen(input, "r");
    char tapeContent[10];
    struct instructions *instructList = malloc(sizeof(struct instructions) * 100);
    struct instructions in;
    static int numStates;
    int startState, endState, iState, iNewState;
    static int instructionCount = 0;
    char iReadVal, iWriteVal, dir;
    static int collNum = 255; //255 ASCII characters

    //Get first half of txt, get needed info to initialize data structures
    if (fscanf(fPointer, "%s %d %d %d", tapeContent, &numStates, &startState, &endState));

    //Create [][], allocate memory.
    struct instructions **arr = malloc(numStates * sizeof(int));
    for (int i = 0; i < numStates; i++) {
        arr[i] = malloc(collNum * sizeof(struct instructions));
    }

    //create [] of instructions, used to initialize [][] below
    while ((fgets((char *) instructList, sizeof(numStates), fPointer))) {
        int i = 0;
        while (fscanf(fPointer, "%d %c %c %c %d", &iState, &iReadVal, &iWriteVal, &dir, &iNewState) == 5) {
            instructList[i] = createInst(iState, iReadVal, iWriteVal, dir, iNewState);
            i++;
            instructionCount++;
        }
    }

    //populate [][]
    for (int k = 0; k < instructionCount; k++) {
        in = createInst(instructList[k].currState, instructList[k].readVal,instructList[k].writeVal, instructList[k].direction,instructList[k].newState);
        arr[in.currState][in.readVal] = in;
    }

    //create/initialize linkedList, populate nodes with input from txt
    struct Node *hed = malloc(sizeof(struct Node));
    int i = 1;
    hed-> data = tapeContent[0];
    while(1) {
        if (tapeContent[i] == '\0') break;
        hed = populate(hed, RIGHT, tapeContent[i]);
        i++;
    }

    ll->head = hed;
    ll->array = arr;
    ll->endTape = endState;
    ll->startTape = startState;

    return *ll;

}

enum moveDirection enumName(enum moveDirection d) {
    if (d == 'L') return LEFT;
    if (d == 'R') return RIGHT;
}

struct Node *navigate(struct Node *head, enum moveDirection d, char c) {
    if (enumName(d) == LEFT) {
      if (head->prev == NULL)
          head = populate(head, LEFT, c);
          //if at the edge of list, create new node on left
          return head->prev;
    }
    if (enumName(d) == RIGHT) {
        if (head->next == NULL)
          head = populate(head, RIGHT, c);
        //if at the edge of list, create new node on right
        return head->next;
}
    return NULL;
}

void begin(struct linkedList ll){
        const char empty = 'B';
        //represent empty Node.data
        char currData;

        int currState = ll.startTape;
        struct instructions currInstruct;
        printf("Start: ");
        printLinkedList(ll);
        while (1) {
            if (currState == ll.endTape) {
                printf("Done");
                break;
            } else {
                printLinkedList(ll);
            }

            currData = ll.head->data;
            //data being read by the head of TM
            currInstruct = getInstructions(ll.array, currState, currData);
            //gets instruction from arr[][]
            currState = currInstruct.newState;
            //store new state
            ll.head->data = currInstruct.writeVal;
            //write new value to head
            ll.head = navigate(ll.head, currInstruct.direction, empty);
            //go either left/right based on direction
     }
}

    int main() {
    printf("Please enter your file name.\n");
    char fileName[100];
    scanf("%s",fileName);
    printf("Magic is happening, please wait.\n");
    struct linkedList ll = initList(fileName);
    begin(ll);
    freeLinkedList(ll.head);
    return 0;
    }
