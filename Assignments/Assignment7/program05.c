// Write a program which accept number and print firs 5 multiple of that number

#include<stdio.h>

void Display(int iNo)
{
    int iCnt = 0;
    int iRet = 0;

    for(iCnt = 1; iCnt <= iNo; iCnt++)
    {
        iRet = iRet + iNo;

        printf("%d\t",iRet);
    }

}

int main()
{
    int iValue = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    Display(iValue);


    return 0;
}



