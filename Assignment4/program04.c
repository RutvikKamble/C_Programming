// Write a program which accept numberfrom user and return summation of all its non factor

#include<stdio.h>

int SumNonFact(int iNo)
{
    int iCnt = 0;
    int iSum = 0;
    
    for(iCnt = iNo; iCnt > 0; iCnt--)
    {
        if(iNo % iCnt != 0)
        {
            iSum = iSum + iCnt;
        }
    }
    return iSum;
}


int main()
{
    int iValue = 0;
    int iRet = 0;
    
    printf("Enter the number :\n");
    scanf("%d",&iValue);

    iRet = SumNonFact(iValue);

    printf("%d", iRet);

    return 0;
}