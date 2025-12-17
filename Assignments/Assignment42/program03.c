//  WAP which returns addition of all elements from singly linear linked list

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
    int iSum = 0, iCnt = 0;


    printf("Input linked list : ");
    while((head -> Next) != NULL)
    {
        printf("| %d |->",head -> Data);
        iSum = iSum + head -> Data;
        head = head -> Next;
    }
    printf("| %d |->",head -> Data);
    printf("NULL\n");
    
    return iSum + head -> Data;

}

/*
int Display(PNODE head)
{
    int iSum = 0, iCnt = 0, iCount = 0;

    while ((head -> Next) != NULL)
    {
       iCount++;
    }
    iCount++;
    
    printf("Input linked list : ");
    for(iCnt = 1; iCnt <= iCount; iCnt++)
    {
        printf("| %d |->",head -> Data);
        iSum = iSum + head -> Data;
        head = head -> Next;
    }
    printf("| %d |->",head -> Data);
    printf("NULL\n");
    
    return iSum + head -> Data;
}
*/

int main()
{
    PNODE First = NULL;
    int iRet = 0;

    InsertFirst(&First, 80);
    InsertFirst(&First, 30);
    InsertFirst(&First, 50);
    InsertFirst(&First, 40);
    InsertFirst(&First, 30);
    InsertFirst(&First, 20);
    InsertFirst(&First, 10);

    iRet = Display(First);

    printf("Addition of all elements is : %d",iRet);

    return 0;
}