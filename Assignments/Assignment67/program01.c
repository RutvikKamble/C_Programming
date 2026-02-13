// Write a program which accept one number and position from user and count number of ON (1) bits in it withot using % & / operator

#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


int CountOnBytes(UINT iNo)
{
    int Mask = 0x1;
    int iCount = 0;
    int Result = 0;

    while(iNo != 0)
    {
        Result = iNo & Mask;
        if(Result == Mask)
        {
            iCount++;
        }
        iNo = iNo >> 1;

    }

    return iCount;
}

int main()
{
    UINT iValue = 0, iRet = 0;
    int iNo = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = CountOnBytes(iValue); 

    printf("The total ON Bit's are %d",iRet);

    
    return 0;
}