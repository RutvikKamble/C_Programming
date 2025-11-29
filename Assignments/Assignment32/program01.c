/*
    Accept number of row and number of column and dislay the below pattern

    Input   :   Row - 4
                Column - 4

Output  :   *   *   *   #  
            *   *   #   *
            *   #   *   *
            #   *   *   *  
*/              
#include<stdio.h>


void pattern(int iRow, int iCol)
{
    int i = 0, j = 0, iNo1 = 0, iNo2 = 0;

    iNo1 = iCol;
    for(i = 1; i <= iRow; i++)
    {

        for(j = 1; j <= iCol; j++)
        {
            if(j == iNo1)
            {
                printf("#\t");
            }
            else
            {
                printf("*\t");
            }
        }
        iNo1--;
        printf("\n");
    }
}

int main()
{
    int iValue1 = 0, iValue2 = 0;

    printf("Enter the Value of Row : ");
    scanf("%d",&iValue1);

    printf("Enter the Value of Column : ");
    scanf("%d",&iValue2);

    pattern(iValue1,iValue2);

    return 0;
}