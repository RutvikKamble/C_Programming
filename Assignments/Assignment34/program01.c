// WAP Which displays ASCII table.
// Table contain Decimal, Hexadecimal, and octal representation of every member from 0 to 255.

#include<stdio.h>

void Display(char Ch, int iNo)
{
    int iCnt = 0;

    printf("Decimal, Hexadecimal or octal values of Characters\n");
    printf("Character\t,Decimal\t, Hexadecimal\t,Octal");
    printf("\n");

    for(iCnt = 0; iCnt < iNo; iCnt++)
    {
        printf("%c\t\t%d\t\t%x\t\t%o\n",Ch,Ch,Ch,Ch);
        Ch++;
    }  
}

int main()
{
    char cValue = 0;
    int iValue = 0;

    cValue = 0;
    iValue = 255;

    Display(cValue,iValue);
    
}

/*
for(iCnt = 1; iCnt <= iNo; iCnt++)
    {
        
    }   


void CheckAlphabet(char Ch, int iNo)
{
    int iCnt = 0;

    printf("Decimal, Hexadecimal or octal values of Characters");
    printf("Decimal\t, Hexadecimal\t,Octal");
    printf("\n");

    for(iCnt = 1; iCnt <= Ch; iCnt++)
    {
        if(Ch >= 'A' && Ch <= 'Z' || Ch >= 'a' && Ch <= 'z')
        {
            printf("%d\t\t%x\t%o",Ch,Ch,Ch);
        }
    }

    printf("%d\t\t%x\t%o",Ch,Ch,Ch);
    
}
*/