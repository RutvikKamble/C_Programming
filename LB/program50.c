// factor of 6

#include<stdio.h>

void DisplayFactors(iNo)
{
    int iCnt = 0;
    // Updater
    if(iNo < 0 )
    {
        iNo = - iNo;
    }
    
    for(iCnt = 1; iCnt <= (iNo/2); iCnt++)
    {
        if ((iNo % iCnt) == 0)
        {
            printf("%d\n",iCnt);
        }
    }
}

// Time Complexity : o(N/2)

int main()
{
    int iValue = 0;
    
    printf("Enter number : \n");
    scanf("%d",iValue);

    DisplayFactors();

    return 0;
}