//  Display first occurance of number in LL 

/*
    Algorithm
        Start
            Accept the N values from user
            and
            Check from that the total Odd values 
        Stop
*/

/////////////////////////////////////////////////////////////////////////////////
//                                                                             //
//  Required Header file                                                       //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

// Typedef
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
//  Date    :       30/12/2025                                                 //
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
//  Date    :       30/12/2025                                                 //
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
//  Description :   Display the all emement from Ll                            //
//  Input   :       int*                                                       //
//  Output  :       void                                                       //
//  Author  :       Rutvik Bibhishan Kamble                                    //
//  Date    :       30/12/2025                                                 //
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
//  Function Name : FirstOccurance                                             //
//  Description :   Find the first occurance of the first number               //
//  Input   :       int*                                                       //
//  Output  :       int                                                        //
//  Author  :       Rutvik Bibhishan Kamble                                    //
//  Date    :       30/12/2025                                                 //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

int FirstOccurance(PNODE first, int no)
{
    int iCount1 = 0, iCount2= 0;

    while(first != NULL)
    {
        iCount1++;
        if(first->Data == no)
        {
            iCount2++;
            break;
        }
        first = first->next;
    }
    if(iCount2 > 0)
    {
        return iCount1;
    }
    else
    {
        return iCount2;
    }
}

/*
int FirstOccurance(PNODE first, int no)
{
    int pos = 1;

    while(first != NULL)
    {
        if(first->Data == no)
        {
            return pos;
        }
        first = first->next;
        pos++;
    }
    return 0;
}

*/

/////////////////////////////////////////////////////////////////////////////////
//                                                                             //
//  Entry point functon of the program                                         //
//                                                                             //
/////////////////////////////////////////////////////////////////////////////////

int main()
{
    int iValue = 0, iRet = 0,iCnt = 0, iNo1 = 0,iNo2 = 0;

    struct node *head = NULL;

    printf("Enter the number for node : ");
    scanf("%d",&iValue);

    for(iCnt = 1; iCnt <= iValue; iCnt++)
    {
        printf("Enter teh data for node : ");
        scanf("%d",&iNo1);

        InsertLast(&head,iNo1);
    }

    printf("Enter the number for find the first occurance :");
    scanf("%d",&iNo2);

    Display(head);
    iRet = FirstOccurance(head,iNo2);

    if (iRet == 0)
    {
        printf("The LL is not Contain the %d",iNo2);   
    }
    else
    {
        printf("The first occurance of the given number is : %d",iRet);
    }

    return 0;
}

/////////////////////////////////////////////////////////////////////////////////
// End of main                                                                 //
/////////////////////////////////////////////////////////////////////////////////