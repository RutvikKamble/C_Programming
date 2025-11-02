// Writ a program which accept one number from user and check whether that nmber is greater that 100 or not

#include<stdio.h>
#include<stdbool.h>

bool CheckGreaterSmaller(int iNo)
{
    int iVlaue = 0;

    iVlaue = 100;
    
    if(iNo >= iVlaue)
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

    printf("Enter the number : ");
    scanf("%d",&iValue);

    bRet = CheckGreaterSmaller(iValue);

    if(bRet == true)
    {
        printf("Greter");
    }
    else
    {
        printf("Smaller");
    }

    return 0;
}