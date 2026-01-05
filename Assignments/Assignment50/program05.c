// Display node at Nth position.

/*
    Algorithm
        Start
            Accept the N values from user
            and 
            Display the node which is Nth position. 
        Stop
*/

/////////////////////////////////////////////////////////////////////////////////
//                                                                             //
//  Required Header file                                                       //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

#include<stdio.h>
#include<stdlib.h>

// Define
#define TRUE 1
#define FALSE 0

// Typedef
typedef int BOOL;

struct node
{
    int Data;
    struct node *next;
};

typedef struct node NODE;
typedef struct node * PNODE;
typedef struct node ** PPNODE;

/////////////////////////////////////////////////////////////////////////////////
//                                                                             //
//  Function Name : InsertFirst                                                //
//  Description :   Insert the value on the first of LL                        //
//  Input   :       int                                                        //
//  Output  :       void                                                       //
//  Author  :       Rutvik Bibhishan Kamble                                    //
//  Date    :       31/12/2025                                                 //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

void InsertFirst(struct node **first, int no)
{
    struct node *newn = NULL;

    newn = (struct node *)malloc(sizeof(struct node));

    newn->Data = no;
    newn->next = NULL;

    if(*first == NULL)
    {
        *first = newn;
    }
    else
    {
        newn->next = *first;
        *first = newn;
    }
}

/////////////////////////////////////////////////////////////////////////////////
//                                                                             //
//  Function Name : InsertLast                                                 //
//  Description :   Insert the value on last of the LL                         //
//  Input   :       int                                                        //
//  Output  :       void                                                       //
//  Author  :       Rutvik Bibhishan Kamble                                    //
//  Date    :       31/12/2025                                                 //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

void InsertLast(PPNODE first, int no)
{
    PNODE newn = NULL;
    PNODE temp = NULL;

    newn = (PNODE)malloc(sizeof(NODE));

    newn->Data = no;
    newn->next = NULL;

    if(*first == NULL)
    {
        *first = newn;
    }
    else
    {
        temp = *first;
        while (temp->next != NULL)
        {
            temp = temp->next;
        }
        temp->next = newn;
    }
}

/////////////////////////////////////////////////////////////////////////////////
//                                                                             //
//  Function Name : Display                                                    //
//  Description :   Display the all emement from LL                            //
//  Input   :       int                                                        //
//  Output  :       void                                                       //
//  Author  :       Rutvik Bibhishan Kamble                                    //
//  Date    :       31/12/2025                                                 //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

void Display(struct node *first)
{
    while(first != NULL)
    {
        printf("|%d|->",first->Data);
        first = first->next;
    }
    printf("NULL\n");
}

/////////////////////////////////////////////////////////////////////////////////
//                                                                             //
//  Function Name : DisplayNthPos                                              //
//  Description :   Display the value of Nth position                          //
//  Input   :       int                                                        //
//  Output  :       void                                                       //
//  Author  :       Rutvik Bibhishan Kamble                                    //
//  Date    :       05/01/2026                                                 //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

DisplayNthPos(PNODE first, int no, int pos)
{
    PNODE temp = NULL;
    int iCount = 0;

    temp = first;

    if(pos < 1 || pos > no)
    {
        return;
    }

    printf("Odd Position numbers : ");
    while (temp != NULL)
    {
        iCount++;
        if (iCount == pos)
        {
            printf("%d\t",temp->Data);
            break;
        }
        temp = temp->next;
    }
}

/////////////////////////////////////////////////////////////////////////////////
//                                                                             //
//  Entry point functon of the program                                         //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

int main()
{
    int iValue1 = 0, iValue2 = 0, iCnt = 0, iNo1 = 0, iRet = 0;

    struct node *head = NULL;

    printf("Enter the number for node : ");
    scanf("%d",&iValue1);

    for(iCnt = 1; iCnt <= iValue1; iCnt++)
    {
        printf("Enter the data for node : ");
        scanf("%d",&iNo1);

        InsertLast(&head,iNo1);
    }

    printf("Enter the Value for position : ");
    scanf("%d",&iValue1);

    Display(head);  
    DisplayNthPos(head,iValue1,iValue2);

    return 0;
}

/////////////////////////////////////////////////////////////////////////////////
// End of main                                                                 //
///////////////////////////////////////////////////////////////////////////////// 