// WAP to accept number from user and display its table in reverse order

#include<stdio.h>

void Table(int iNo)
{
    int iCnt = 0;
    int iValue = 0;

    for(iCnt = 10; iCnt > 0; iCnt--)
    {
        iValue = iNo * iCnt;
        printf("%d\n",iValue);

        iValue = 0;
    }
}

int main()
{
    int iValue = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    Table(iValue);

    return 0;
}