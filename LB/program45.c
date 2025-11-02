// factor of 6

#include<stdio.h>

// Wrong Approach

void DisplayFactors(iNo)
{
    if ((iNo % 1) == 0)
    {
        printf("2\n");
    }
    if ((iNo % 2) == 0)
    {
        printf("2\n");
    }
    if ((iNo % 3) == 0)
    {
        printf("2\n");
    }
    if ((iNo % 4) == 0)
    {
        printf("2\n");
    }
    if ((iNo % 5) == 0)
    {
        printf("2\n");
    }
        
}

int main()
{
    int iValue = 0;
    
    //printf("Enter number : \n");
    //scanf("%d",iValue);

    DisplayFactors();

    return 0;
}