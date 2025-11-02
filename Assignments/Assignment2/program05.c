// Accept number from user and check whether number is even or odd

#include<stdio.h>
#include<stdbool.h>

bool CheckDivisible(int iNo1)
{
    if(iNo1 % 2 == 0)
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
    int iValue1 = 0;
    bool bRet = false;

    printf("Enter the number : ");
    scanf("%d",&iValue1);

    bRet = CheckDivisible(iValue1);

    if (bRet == true)
    {
        printf("The number is Even\n");
    }
    else
    {
        printf("The number is Odd \n");
    }

    return 0;
}