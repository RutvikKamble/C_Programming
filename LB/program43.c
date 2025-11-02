// Input : 12 / 2      output : true
// Input : 12 / 3      output : true
// Input : 12 / 5      output : flase
// Input : 12 / 12     output : true
// Input : 12 / 7      output : false

#include<stdio.h>

int main()
{
    int iValue1 = 0, iValue2 = 0;

    printf("Enter first number : \n");
    scanf("%d",iValue1);

    printf("Enter first number : \n");
    scanf("%d",iValue2);

    if (iValue1 % iValue2 == 0)
    {
        printf("It is completely devisible\n");
    }
    else
    {
        printf("It is not divisible\n");
    }

    return 0;
}