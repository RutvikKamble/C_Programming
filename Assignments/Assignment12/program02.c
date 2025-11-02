// Calculate total number of factro

# include<stdio.h>

int Pritn_factor(int number)
{
    int iCnt = 0,iCount = 0;

    for(iCnt = 1; iCnt <= number/2; iCnt++)
    {
        if(number%iCnt == 0)
        {
            iCount = iCount + 1;
        }
    }
    return iCount;
}

int main()
{
    int number = 0, iCnt = 0;

    printf("Enter number : ");
    scanf("%d",&number);

    iCnt = Pritn_factor(number);

    printf("Total factor is : %d",iCnt);

    return 0;
}


// Time Complexity  =   N/2