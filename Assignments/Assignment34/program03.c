// Accept the character from user
// If it it is Capital then display all the character till Z.
// If input character is small then print all the characters in reverse order till a.
// In other cases display directly


#include<stdio.h>

void Display(char Ch)
{

    if(Ch >= 65 && Ch <= 90)
    {
        while (Ch <= 90)
        {
            printf("%c\t",Ch);
            Ch++;
        }
        
    }
    else if(Ch >= 97 && Ch <= 122)
    {
        while (Ch >= 97)
        {
            printf("%c\t",Ch);
            Ch--;
        }
    }
    else
    {
        printf("%c\t",Ch);
    }
}

int main()
{
    char cValue = 0;

    printf("Enter the Character : ");
    scanf("%c",&cValue);

    Display(cValue);
    
}