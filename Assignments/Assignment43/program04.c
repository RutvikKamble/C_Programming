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
    int iMax = 0;
    int SecMax = 0;

    if(first == NULL)
    {
        return -1;
    }

    iMax = first->data;
    first = first->next;

    while(first != NULL)
    {
        if(first->data > iMax)
        {
            SecMax = iMax;
            iMax = first->data;
        }
        else if(first->data > SecMax && first->data != iMax)
        {
            SecMax = first->data;
        }

        first = first->next;
    }
    
    return SecMax;
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



