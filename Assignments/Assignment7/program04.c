// Write a program which accept number from user and print its numbers line

#include<stdio.h>

void Display(int iNo)
{
    int iCnt = 0;

    for(iCnt = 1; iCnt < iNo; iCnt++)
    {
        if(iCnt%2 != 0)
        {
            printf("%d\t",iCnt);
        }
        
    }

}

int main()
{
    int iValue = 0;

    printf("Enter the first number : ");
    scanf("%d",&iValue);

    Display(iValue);

    return 0;
}