// Write a program which accept number from user and return difference between summation of all its factors and non factrows

#include<stdio.h>

int SumNonFact(int iNo)
{
    int iCnt = 0;
    int iSumFact = 0;
    int iSum = 0;
    int diff = 0;
    
    for(iCnt = iNo; iCnt > 0; iCnt--)
    {
        if(iNo % iCnt == 0)
        {
            iSumFact = iSumFact + iCnt;
        }
        else
        {   
            iSum = iSum + iCnt;
        }
    }

    diff = iSumFact - iSum;

    return diff;
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