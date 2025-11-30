// Accept character from user and display its ascii value int Decimal, Octal nad Hexadecimal format.

#include<stdio.h>

void Display(char Ch)
{

    printf("%c\t\n",Ch);
    printf("Decimal : %d\t\t\n",Ch);
    printf("Octal : %o\t\t\n",Ch);
    printf("Hexadecimal : %x\t\n",Ch);
    

}

int main()
{
    char cValue = 0;

    printf("Enter the Character : ");
    scanf("%c",&cValue);

    Display(cValue);
    
}