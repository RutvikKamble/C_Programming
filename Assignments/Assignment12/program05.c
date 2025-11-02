// Check number is divisible by 5

#include<stdio.h>
#include<stdbool.h>

bool CheckDiv(int iNo)
{
    if(iNo % 5 == 0)
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
    bool iRes = false;

    printf("Enter the number : ");
    scanf("%d", iValue);

    iRes = CheckDiv(iValue);

    if(iRes = true)
    {
        printf("Yes, The number is divisible by 5 ");
    }
    else
    {
        printf("No, The number is not divisible by 5 ");
    }

    return 0;
}