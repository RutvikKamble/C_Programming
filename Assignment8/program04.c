// WAP which accept number from user and display its tabel

#include<stdio.h>

void Table(int iNo)
{
    int iCnt = 0;
    int iValue = 0;

    for(iCnt = 10; iCnt > 0; iCnt--)
    {
        iValue = iValue + iNo;
        printf("%d\n",iValue);
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