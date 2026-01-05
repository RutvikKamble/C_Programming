// Display Elements Greater Than Average.

/*
    Algorithm
        Start
            Accept the N values from user
            and 
            Display the all elements which are greter than the Average. 
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
//  Function Name : DisplayGreaterThanAvg                                      //
//  Description :   Display all Element which are Greter than the Average      //
//  Input   :       int                                                        //
//  Output  :       void                                                       //
//  Author  :       Rutvik Bibhishan Kamble                                    //
//  Date    :       04/01/2026                                                 //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

void DisplayGreaterThanAvg(PNODE first)
{
    if(first == NULL)
    {
        printf("Linked list is empty\n");
        return;
    }

    PNODE temp = NULL;
    int iCount = 0, iSum = 0;
    float fAvg = 0.0f;

    temp = first;

    while (temp != NULL)
    {
        iSum = iSum + temp->Data;
        iCount++;
        temp = temp->next;
    }

    fAvg = iSum/iCount;
    temp = first;

    printf("The values which are greter than Average :\t\t");
    while(temp != NULL)
    {
        if(temp->Data > fAvg)
        {
            printf("%d\t",temp->Data);
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
    int iValue = 0,iCnt = 0, iNo1 = 0, iRet = 0;

    struct node *head = NULL;

    printf("Enter the number for node : ");
    scanf("%d",&iValue);

    for(iCnt = 1; iCnt <= iValue; iCnt++)
    {
        printf("Enter the data for node : ");
        scanf("%d",&iNo1);

        InsertLast(&head,iNo1);
    }

    Display(head);
    DisplayGreaterThanAvg(head);

    return 0;
}

/////////////////////////////////////////////////////////////////////////////////
// End of main                                                                 //
///////////////////////////////////////////////////////////////////////////////// 