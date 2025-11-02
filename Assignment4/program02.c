// write a program which accept number from user and display its factors in decressing order

#include<stdio.h>

void RevFact(int iNo)
{
    int iCnt = 0;
    int iValue = 0;

    for(iCnt = iNo/2; iCnt > 0; iCnt--)
    {
        if(iNo % iCnt == 0)
        {
            printf("%d\t",iCnt);
        }
    }
}



int main()
{
    int iValue = 0;
    int iRet = 0;

    printf("Enter the number :\n");
    scanf("%d",&iValue);

    RevFact(iValue);

    return 0;
}