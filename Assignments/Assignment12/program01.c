// Print all factros of a number

# include<stdio.h>

void Pritn_factor(int number)
{
    int iCnt = 0;

    for(iCnt = 1; iCnt <= number/2; iCnt++)
    {
        if(number%iCnt == 0)
        {
            printf("%d\t",iCnt);
        }
    }
}

int main()
{
    int number = 0;

    printf("Enter number : ");
    scanf("%d",&number);

    Pritn_factor(number);

    return 0;
}


// Time Complexity  =   N/2