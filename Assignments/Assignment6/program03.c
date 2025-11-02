// Write a program which accept two numbers and check whether numbersare equal or not.

#include<stdio.h>
#include<stdbool.h>

bool CheckEqual(int iNo1, int iNo2)
{
    bool bRet = false;

    if(iNo1 == iNo2)
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
    
    int iValue1 = 0 , iValue2 = 0;
    bool iResult = false;

    printf("Enter Two numbers : ");
    scanf("%d %d",&iValue1, &iValue2);

    iResult = CheckEqual(iValue1,iValue2);

    if(iResult == true)
    {
        printf("These are equal");
    }
    else
    {
        printf("These are not equal");
    }

    return 0;
}