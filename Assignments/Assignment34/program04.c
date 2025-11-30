// Accept character from user and check wherther it is special symbol or not\

#include<stdio.h>

void Display(char Ch)
{

    if((Ch >= 33 && Ch <= 47) || (Ch >= 58 && Ch <= 64) ||
       (Ch >= 33 && Ch <= 47) || (Ch >= 91 && Ch <= 96) || 
       (Ch >= 123 && Ch <= 126))
    {
        printf("Yes, it is Special Symbol %c\t",Ch);
    }
    else
    {
        printf("No, it is not Special Symbol %c\t",Ch);
    }
}

int main()
{
    char cValue = 0;

    printf("Enter the Character : ");
    scanf("%c",&cValue);

    Display(cValue);
    
}