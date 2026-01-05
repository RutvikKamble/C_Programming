// Sum of Digit of Each node

/*
    Algorithm
        Start
            Accept the N values from user
            and 
            print the Sum of digit frmo each node. 
        Stop
*/

/////////////////////////////////////////////////////////////////////////////////
//                                                                             //
//  Required Header file                                                       //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

#include<stdio.h>
#include<stdlib.h>

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
//  Function Name : SumDigit                                                   //
//  Description :   Sum of digit of each node                                  //
//  Input   :       int                                                        //
//  Output  :       void                                                       //
//  Author  :       Rutvik Bibhishan Kamble                                    //
//  Date    :       03/01/2026                                                 //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

void SumDigit(PNODE first)
{
    PNODE temp = NULL;

    printf("The Sum of digit from each node :\t");
    temp = first;
    while (temp != NULL)
    {
        int iSum = 0, iDigit = 0;

        iDigit = temp->Data;

        while(iDigit != 0)
        {
            iSum = iSum + iDigit % 10;
            iDigit = iDigit / 10;
        }
        printf("%d\t",iSum);
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
    SumDigit(head);

    return 0;
}

/////////////////////////////////////////////////////////////////////////////////
// End of main                                                                 //
/////////////////////////////////////////////////////////////////////////////////