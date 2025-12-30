//  Check whether the number is present or not

#include<stdio.h>
#include<stdlib.h>

#define TRUE 1
#define FALSE 0

typedef int BOOL;


struct node
{
    int Data;
    struct node *next;
};

typedef struct node NODE;
typedef struct node * PNODE;
typedef struct node ** PPNODE;

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

void Display(struct node *first)
{
    while(first != NULL)
    {
        printf("|%d|->",first->Data);
        first = first->next;
    }
    printf("NULL\n");
}

BOOL IsPresent(PNODE first, int no)
{
    int iCount = 0;

    while(first != NULL)
    {
        if(first->Data == no)
        {
            iCount++;
            break;
        }
        first = first->next;
    }

    if(iCount > 0)
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    }   
}


int main()
{
    int iValue = 0, iRet = 0,iCnt = 0, iNo1 = 0, iNo2 = 0;
    BOOL bRet = FALSE;

    struct node *head = NULL;

    printf("Enter the number for node : ");
    scanf("%d",&iValue);

    for(iCnt = 1; iCnt <= iValue; iCnt++)
    {
        printf("Enter teh data for node : ");
        scanf("%d",&iNo1);

        InsertLast(&head,iNo1);
    }

    printf("Enter the number to find in LL : ");
    scanf("%d",&iNo2);

    Display(head);

    bRet = IsPresent(head,iNo2);

    if(bRet == TRUE)
    {
        printf("The %d is present in the LL",iNo2);
    }
    else
    {
        printf("The %d is not present in the LL",iNo2);
    }

    return 0;
}