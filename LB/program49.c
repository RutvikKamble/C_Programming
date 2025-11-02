// factor of 6

#include<stdio.h>

void DisplayFactors(iNo)
{
    // Updater
    if(iNo < 0 )
    {
        iNo = - iNo;
    }
    int iCnt = 0;

    for(iCnt = 1; iCnt < iNo; iCnt++)
    {
        if ((iNo % iCnt) == 0)
        {
            printf("%d\n",iCnt);
        }
    }
}

// Time Complexity : o(N)

int main()
{
    int iValue = 0;
    
    //printf("Enter number : \n");
    //scanf("%d",iValue);

    DisplayFactors();

    return 0;
}