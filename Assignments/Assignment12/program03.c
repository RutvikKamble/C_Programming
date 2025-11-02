// Sum of all factor of a number

# include<stdio.h>

int Pritn_factor(int number)
{
    int iCnt = 0,iCount = 0;

    for(iCnt = 1; iCnt <= number/2; iCnt++)
    {
        if(number%iCnt == 0)
        {
            iCount = iCount + iCnt;
        }
    }
    return iCount;
}

int main()
{
    int number = 0, iCnt;

    printf("Enter number : ");
    scanf("%d",&number);

    iCnt = Pritn_factor(number);

    printf("Sum of all factor : %d", iCnt);

    return 0;
}


// Time Complexity  =   N/2