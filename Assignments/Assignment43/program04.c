// WAP which display second maximum number from linked list
#include<stdio.h>
#include<stdlib.h>

struct node
{
    int data;
    struct node *next;
};

typedef struct node NODE;
typedef struct node* PNODE;
typedef struct node** PPNODE;


void InsertFist(struct node **first, int no)
{
    struct node * newn = NULL;

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

void Display(PNODE first)
{
    while(first != NULL)
    {
        printf("| %d |->",first->data);
        first = first->next;
    }
    printf("NULL\n");
}

int DisplaySecLast(PNODE first)
{
    int SecLast = 0;

    if(first == NULL || first->next == NULL)
    {
        printf("Linked list not contain more than one elements\n");
        return 0;
    }
    else
    {
        while (first->next->next != NULL)
        {
        first = first->next;
        }
        SecLast = first->next->data;

        return SecLast;
    }
}


int main()
{
    int iValue = 0, iCnt = 0, iNo = 0, iRet = 0;

    struct node * head = NULL;    
    

    printf("Enter the number for the node : ");
    scanf("%d",&iValue);

    for(iCnt = 1; iCnt<= iValue; iCnt++)
    {
        printf("Enter the data for node : ");
        scanf("%d",&iNo);

        InsertFist(&head,iNo);
    }

    Display(head);

    iRet = DisplaySecLast(head);

    printf("Second last element is : %d ",iRet);


    return 0;
}



