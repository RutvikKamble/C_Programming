/*
    Accept number of row and number of column and dislay the below pattern

    Input   :   Row - 5
                Column - 5

Output  :   1   2   3   4   5
            1   2           5
            1       3       5
            1           4   5
            1   2   3   4   5
*/              
#include<stdio.h>


void pattern(int iRow, int iCol)
{
    int i = 0, j = 0, iNo1 = 0, iNo2 = 0;

    for(i = 1; i <= iRow; i++)
    {

        for(j = 1; j <= iCol; j++)
        {
            if(j == i || i == 1 || i == iRow || j == 1 || j == iCol)
            {
                printf("%d\t",j);
            }
            else
            {
                printf("\t");
            }
        }
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