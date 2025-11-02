//  2   4   6   8 
#include<stdio.h>

void Display(int iNo)
{
    int iCnt = 0;

    if((iCnt % 2) == 0)
    {
        printf("%d\t",iCnt);
    }

    printf("\n");
}

// Time complexity = O(N)

int main()
{
    int iValue = 0;

    printf("Please enter frequency :\n");
    scanf("%d",&iValue);

    Display(iValue);

    return 0;
}