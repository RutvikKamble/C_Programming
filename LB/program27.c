#include<stdio.h>

// Dynamic Function

void Display(int iFrequency)
{
    int iCnt = 0;

    // Filter
    if (iFrequency<0)
    {
        printf("Invalid Input");
        
        return;
    }
    for(iCnt = 1; iCnt <= iFrequency; iCnt++)
    {
        printf("Jay Ganesh...\n");
    }
}

// It should be int main

int main()
{
    int iCount =0;

    printf("Enter the frequency :\n");
    scanf("%d",&iCount);

    Display(iCount);

    return 0;
}

