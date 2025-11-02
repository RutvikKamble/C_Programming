/* Write a program whic haccept number from user and if nubmer is less than 50
then print small, if it is greater than 50 and less than 100 print medium,if it is
greter than 100 then print large. */

#include<stdio.h>

void Number(int iNo)
{
    if(iNo <= 50)
    {
        printf("Small");
    }
    else if (iNo > 50 && iNo < 100)
    {
        printf("Medium");
    }
    else
    {
        printf("Large");
    }
}

int main()
{
    int iValue = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    Number(iValue);

    return 0;
}