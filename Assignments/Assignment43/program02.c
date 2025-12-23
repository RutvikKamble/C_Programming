// WAP which display all elements which are prime from singly linear linked list

#include<stdio.h>
#include<stdlib.h>

struct node
{
    int data;
    struct node *next;
};

typedef struct node NODE;
typedef struct node *PNODE;
typedef struct node **PPNODE;


void InsertFirst(PPNODE first, int no)
{
    PNODE newn = NULL;

    newn = (struct node *)malloc(sizeof(struct node));

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

void InsertLast(PPNODE first, int no)
{
    PNODE newn = NULL;
    PNODE temp = NULL;

    newn = (PNODE)malloc(sizeof(NODE));

    newn->data = no;
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

void Display(PNODE first)
{
    while (first != NULL)
    {
        printf("| %d |->",first->data);
        first = first->next;
    }
    printf("NULL\n");
}

void DisplayPrime(PNODE first)
{
    int iCnt = 0, iCount = 0;

    while(first != NULL)
    {
        iCount = 0;
        for(iCnt = 2; iCnt <= first->data/2; iCnt++)
        {
            if (first->data % iCnt == 0)
            {
                iCount++;
                break;
            }
        }
        if(iCount == 0)
        {
            printf("%d \t",first->data);
        }
        first = first->next;
    }
}

int main()
{
    int iCnt = 0, iValue = 0, iNo = 0;
    
    PNODE head = NULL;

    printf("Enter the number for node : ");
    scanf("%d",&iValue);

    for(iCnt = 1; iCnt <= iValue; iCnt++)
    {
        printf("Insert the data for node : ");
        scanf("%d",&iNo);

        InsertLast(&head,iNo);
    }

    Display(head);

    printf("The prime number are : \n");
    DisplayPrime(head);

    return 0;
}