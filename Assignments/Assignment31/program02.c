/*
    Accept number of row and number of column and dislay the below pattern

    Input   :   Row - 4
                Column - 4

        Output  :   *   *   *   *
                    *   *   *
                    *   *
                    * 
*/          

#include<stdio.h>

void pattern(int iRow, int iCol)
{
    int i = 0, j = 0;

    for(i = 1; i <= iRow; i++)
    {
        for(j = 1; j <= iCol; j++)
        {
            printf("*\t");   
        }
        iCol--;
        printf("\n");
    }
}

int main()
{
    int iValue1 = 0, iValue2 = 0;

    printf("Enter the number of Row : ");
    scanf("%d",&iValue1);

    printf("Enter the number of Column : ");
    scanf("%d",&iValue2);

    pattern(iValue1,iValue2);

    return 0;
}