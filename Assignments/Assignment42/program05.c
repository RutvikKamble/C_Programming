//  WAP which returns largest number from linked list

#include<stdio.h>
#include<stdlib.h>

#pragma pack(1)

struct node
{
    int Data;
    struct node *Next;
};
typedef struct node NODE;
typedef struct node * PNODE;
typedef struct node ** PPNODE;

void InsertFirst(PPNODE Head, int no)
{
    PNODE newn = NULL;

    newn = (PNODE)malloc(sizeof(NODE));

    newn -> Next = NULL;
    newn -> Data = no;

    if(*Head == NULL)
    {
        *Head = newn;
    }
    else
    {
        newn -> Next = *Head;
        *Head = newn;
    }
}

int Display(PNODE head)
{
    int smallNo = 0;

    PNODE phead = head;

    printf("Input linked list : ");
    while((head -> Next) != NULL)
    {
        printf("| %d |->",head -> Data);
        head = head -> Next;
    }
    printf("| %d |->",head -> Data);
    head = head -> Next;
    printf("NULL\n");

    smallNo = phead -> Data;
    while((phead -> Next) != NULL)
    {
        if((phead -> Data) < smallNo)
        {
           smallNo = (phead -> Data);
        }
        phead = phead -> Next;
    }
    if ((phead -> Data) < smallNo)
    {
        return phead -> Data;
    }
    else
    {    
        return smallNo;
    }
}

int main()
{
    PNODE First = NULL;
    int iRet = 0;

    InsertFirst(&First, 10);
    InsertFirst(&First, 30);
    InsertFirst(&First, 50);
    InsertFirst(&First, 40);
    InsertFirst(&First, 70);
    InsertFirst(&First, 20);
    InsertFirst(&First, 60);

    iRet = Display(First);

    printf("Smallest number from linked list is : %d",iRet);

    return 0;
}