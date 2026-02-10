// Write a program which check whether first and last bit is ON or OFF 
// First Bit means 1 and last bit means 32.



#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0



/*
BOOL Check(UINT iNo)
{
    UINT Mask1 = 0x1;
    UINT Mask2 = 0x1;
    UINT Mask = 0x0;   
    UINT Result = 0;

    Mask2 = Mask2 << 31;

    Mask = Mask1 | Mask2;

    Result = Mask & iNo;

    if(Result == Mask)
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    } 
}
*/

BOOL CheckFirst(UINT iNo)
{
    UINT Mask = 0x1;  
    UINT Result = 0;
    
    Result = Mask & iNo;

    if(Result == Mask)
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    } 
}

BOOL CheckLast(UINT iNo)
{
    UINT Mask = 0x1;  
    UINT Result = 0;

    Mask = Mask << 31;
    
    Result = Mask & iNo;

    if(Result == Mask)
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    } 
}


int main()
{
    UINT iValue = 0;
    int No = 0;
    BOOL iRet = FALSE;

    printf("Enter the number for the check the 1st and 32th bit is on or not : ");
    scanf("%u",&iValue);


    iRet = CheckFirst(iValue);

    if(iRet == TRUE)
    {
        printf("1st bit is ON\n");
    }
    else
    {
        printf("1st bit is OFF\n");
    }

    
    iRet = CheckLast(iValue);

    if(iRet == TRUE)
    {
        printf("32th bit is ON\n");
    }
    else
    {
        printf("32th bit is OFF\n");
    }


    return 0;
} 