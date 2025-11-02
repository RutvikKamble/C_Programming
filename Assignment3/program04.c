// Accept one character from user and convert case of that character

#include<stdio.h>

void DisplayConvert(char ch)
{
    char c = ch;

    if(c == 'a')
    {
        printf("%c",'A');
    }
    else if (c == 'D')
    {
        printf("%c", 'd');
    }
}

int main()
{
    char cValue = 0;

    printf("Enter character     a or D :\n");
    scanf("%c",&cValue);

    DisplayConvert(cValue);

    return 0;
}
