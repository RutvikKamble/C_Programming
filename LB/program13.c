#include<stdio.h>
#include<stdbool.h>

bool CheckEvenOdd(int iNo)
{
    int iRem = 0;

    int iRem = iNo % 2;

    if (iRem == 0)
    {
        return true;
    }
    else
    {
        return false;
    }
}

int main()
{
    int iValue = 0;
    bool bRet = false;

    printf("Ener a number : ");
    scanf("%d",&iValue);

    bRet = CheckEvenOdd(iValue);

    printf("Resuslt is : %d\n",bRet);

    return 0;
}