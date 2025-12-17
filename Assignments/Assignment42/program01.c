//  WAP which search first occurance of perticular element from singly linear linked list
//  Function should return position at which element is found.

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

int Display(PNODE head, int no)
{
    int iCount = 0;

    PNODE phead = head;

    printf("Input linked list : ");
    while((head -> Next) != NULL)
    {
        printf("| %d |->",head -> Data);
        head = head -> Next;
    }
    printf("NULL\n");
    printf("Input Element  : %d \n",no);

    while((phead -> Next) != NULL)
    {
        iCount++;
        if((phead -> Data) == no)
        {
            return iCount;
        }
        phead = phead -> Next;
    }
}

int main()
{
    PNODE First = NULL;
    int iRet = 0;

    InsertFirst(&First, 70);
    InsertFirst(&First, 30);
    InsertFirst(&First, 50);
    InsertFirst(&First, 40);
    InsertFirst(&First, 30);
    InsertFirst(&First, 20);
    InsertFirst(&First, 10);

    iRet = Display(First,30);

    printf("%d",iRet);

    return 0;
}