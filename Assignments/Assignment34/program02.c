// Accept character from user
// If character is small Display it's corresponding capital character,
// and if it small then display it 

#include<stdio.h>

void Display(char Ch)
{
    int iCnt = 0;

    if(Ch >= 97 && Ch <= 122)
    {
        printf("%c",Ch-32);
    }
    else
    {
        printf("%c",Ch);
    }
}

int main()
{
    char cValue = 0;

    printf("Enter the Character : ");
    scanf("%c",&cValue);

    Display(cValue);
    
}