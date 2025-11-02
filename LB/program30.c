// 1    2   3   4   5
#include<stdio.h>

void Display()
{
    int iCnt = 1;
    
    while(iCnt <= 5)
    {
        printf("%d\t",iCnt);
        
        iCnt++;
    }
}

int main()
{
    Display();

    return 0;
}