// Write a program which accept number from user and display all its non factors

#include<stdio.h>

void NonFact(int iNo)
{
    int iCnt = 0;
    
    for(iCnt = iNo; iCnt > 0; iCnt--)
    {
        if(iNo % iCnt != 0)
        {
            printf("%d\t",iCnt);
        }
    }
}


int main()
{
    int iValue = 0;
    
    printf("Enter the number :\n");
    scanf("%d",&iValue);

    NonFact(iValue);

    return 0;
}