// Display the prime number from Linked List

/*
    Algorithm
        Start
            Accept the N values from user
            and 
            print the Prime number from that numbers. 
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

int bFlab = FALSE;

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
//  Function Name : DisplayPrime                                               //
//  Description :   Display the prime numbers from the LL                      //
//  Input   :       int                                                        //
//  Output  :       void                                                       //
//  Author  :       Rutvik Bibhishan Kamble                                    //
//  Date    :       03/01/2026                                                 //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

void DisplayPrime(PNODE first)
{
    PNODE temp = NULL;

    printf("Prime numbers from the linked list :\t");
    temp = first;
    while (temp != NULL)
    {
        int iCnt = 0, iCount = 0;

        for(iCnt = 2; iCnt <= temp->Data/2; iCnt++)
        {   
            if(temp->Data % iCnt == 0)
            {
                iCount++;
                break;
            }
        }

        if(iCount == 0)
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
    int iValue = 0,iCnt = 0, iNo1 = 0;

    struct node *head = NULL;

    printf("Enter the number for node : ");
    scanf("%d",&iValue);

    for(iCnt = 1; iCnt <= iValue; iCnt++)
    {
        printf("Enter teh data for node : ");
        scanf("%d",&iNo1);

        InsertLast(&head,iNo1);
    }

    Display(head);
    DisplayPrime(head);

    return 0;
}

/////////////////////////////////////////////////////////////////////////////////
// End of main                                                                 //
/////////////////////////////////////////////////////////////////////////////////