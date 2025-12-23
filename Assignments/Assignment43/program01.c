// WAP which display all elements which are perfect from singly linear linked list.

#include<stdio.h>
#include<stdlib.h>

#pragma pack(1)

struct node
{
    int data;
    struct node *next;
};

typedef struct node NODE;
typedef struct node * PNODE;
typedef struct node ** PPNODE;


void InsertFirst(PPNODE first, int no)
{
    PNODE newn = NULL;
    newn = (PNODE)malloc(sizeof(NODE));

    newn->data = no;
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

void Display(PNODE first)
{

    while (first != NULL)
    {
        printf("| %d |->",first->data);
        first = first->next;
    }
    printf("NULL\n");
}

void DisplayPerfect(PNODE first)
{
    int iFact = 0, iSum = 0;

    while(first != NULL)
    {
        iSum = 0;
        for(iFact = 1; iFact <= first->data/2; iFact++)
        {
            if (first->data % iFact == 0)
            {
                iSum = iSum + iFact;
            }
        }

        if(iSum == first->data)
        {
            printf("%d\t",first->data);
        }
        first = first->next; 
    }
}


int main()
{
    int iCnt = 0, iValue = 0, iNo = 0;

    PNODE head = NULL;

    printf("Insert number of node : ");
    scanf("%d",&iValue);

    for(iCnt = 1; iCnt <= iValue; iCnt++)
    {
        printf("Insert the data for node : ");
        scanf("%d",&iNo);

        InsertFirst(&head,iNo);
    }

    Display(head);
    DisplayPerfect(head);
    
    return 0;
}