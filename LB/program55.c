#include<stdio.h>
#include<stdbool.h>

int CheckPrime(int iNo)
{
    int iCnt = 0, iFrequency = 0;

    if(iNo < 0 )
    {
        iNo = -iNo;
    }
    
    for(iCnt = 2; iCnt <= (iNo / 2); iCnt++)
    {
        if(iNo % iCnt == 0)
        {
            iFrequency++;
            break;                      //  Optimization
        }
    }

    if(iFrequency == 0)
    {
        return true;                    // No factor
    }
    else
    {
        return false;                   // Atlist one factor
    }
}

int main()
{
    int iValue = 0;
    bool bRet = false;

    printf("Enter the number :");
    scanf("%d",&iValue);

    bRet = CheckPrime(iValue);
    
    if(bRet == true)
    {
        printf("%d is prime number\n",iValue);
    }
    else
    {
        printf("%d is not prime number\n",iValue);
    }
    return 0;
}

/*
    Time Complexity : for prime     - N/2
    Time Complexity : for non prime - either 1 or 2
*/