// WAP which accept range from user and display all numbers in between that range.

#include<stdio.h>

void Range(int iNo1, int iNo2)
{
    int iCnt = 0;

    if(iNo1 <= iNo2)
    {
        for(iCnt = iNo1; iCnt <= iNo2; iCnt++)
        {
            printf("%d\t",iCnt);
        }
    }
    else
    {
        for(iCnt = iNo2; iCnt <= iNo1; iCnt++)
        {
            printf("%d\t",iCnt);
        }
    }
}

int main()
{
    int iValue1 = 0, iValue2 = 0;

    printf("Enter the Range : ");
    scanf("%d %d",&iValue1,&iValue2);

    Range(iValue1,iValue2);

    return 0;
}