// Find Maximum of Two Numbers

#include<stdio.h>

void CheckMax(int iNo1, int iNo2)
{
    if(iNo1 > iNo2)
    {
        printf("First number is Max : %d",iNo1);
    }
    else
    {
        printf("Second number is Max : %d",iNo2);
    }
}

int main()
{
    int iValue1, iValue2;

    printf ("Enter the first number : ");
    scanf("%d",&iValue1);

    printf("Enter the second number : ");
    scanf("%d",&iValue2);

    CheckMax(iValue1,iValue2);

    return 0;
}