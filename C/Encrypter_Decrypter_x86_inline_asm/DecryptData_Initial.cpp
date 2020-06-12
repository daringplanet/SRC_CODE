// DecryptData.cpp
//
// THis file uses the input data and key information to decrypt the input data
//

#include "Main.h"

/////////////////////////////////////////////////////////////////////////////////////////////////
// code to decrypt the data as specified by the project assignment
int decryptData(char *data, int dataLength)
{
	int resulti = 0;
	
	
	gdebug1 = 0;					// a couple of global variables that could be used for debugging
	gdebug2 = 0;					// also can have a breakpoint in C code


	// Set up the stack frame and assign variables in assembly if you need to do so
	// access the parameters BEFORE setting up your own stack frame
	// Also, you cannot use a lot of global variables - work with registers

__asm {
		mov ecx, gNumRounds
			dec ecx
			mov gNumRounds, ecx

START2 :
		xor ecx, ecx
		
			mov edi, data


START :
		cmp ecx, dataLength
		jge DONE
		

E://code table swap
		
		
		
			movzx eax, byte ptr[edi + ecx]
			lea esi, gDecodeTable//gets first value of the table
			movzx al, [esi + eax]
			mov byte ptr[edi + ecx], al//swap data with the new bit from decryption table 
			
			

			
B:   //swapping nibbles
			xor edx, edx
			xor ebx, ebx
			movzx eax, byte ptr[edi + ecx]
			mov dl, al
			mov bl, al



			and dl, 0xf0  //preserving one of the bits 
			ror dl, 4//rotating the bit to the right 4 times 


			and bl, 0x0f//preserving the opposite bit
			ror bl, 4//rotating it again 4 times 


			xor eax, eax
			add eax, ebx
			add eax, edx
			mov byte ptr[edi + ecx], al


			


C:
			//reversing the bit order
				movzx eax, byte ptr[edi + ecx]
				xor ebx, ebx
				xor edx, edx
				//counter to move all the bits 
				mov edx, 7
				//loop to rotate all bits until the order is reversed
LOOP1:
				sar al, 1
				rcl bl, 1 //rotating a bit through the carry 
				test edx, edx
				je CDONE
				dec edx
				jmp LOOP1

CDONE :
				mov al, bl
				mov byte ptr[edi + ecx], al



				



A:
		movzx eax, byte ptr[edi + ecx]
		ror al, 1  //rotating the bit to to the right 
		mov byte ptr[edi + ecx], al





		

D:
		//rotating high nibble right and low nibble left
			xor edx, edx
			xor ebx, ebx
			movzx eax, byte ptr[edi + ecx]
			mov dl, al//gets one byte of the data


			and dl, 0xf0
			ror dl, 1 //rotating the high nibble right
			clc
			bt dl, 3
			jc RH
RO1 :

			mov bl, al
			and bl, 0x0f
			rol bl, 1  //rotaing low nibble left
			clc
			bt bl, 4
			jc RL
			jmp DDONE


RH :  //checking to see if there was an end bit that was rotated out 
			and dl, 0xf0
			or dl, 0x80
			jmp RO1

RL :
			//also checking for end bit but on opposite side 
			and bl, 0x0f
			or bl, 0x01
			jmp DDONE

DDONE :

			xor eax, eax
			add eax, edx
			add eax, ebx
			mov byte ptr[edi + ecx], al
			



			


			inc ecx
			jmp START
		
		
NEWHOP:
	
			mov eax, 0xffff
			mov resulti, eax
			jmp CN


DONE:  //
				xor ecx, ecx
				mov ecx,gNumRounds
				mov ebx, dataLength
				lea edi, gkey
				mov edi, gptrKey
				lea esi, gPasswordHash
				mov esi, gptrPasswordHash



				mov edx, data   //gets data
				mov ebx, edx //moves data into ebx
				add ebx, dataLength
				//setting up the hop count
				xor eax, eax
				mov ah, byte ptr[esi + 2 + ecx * 4]
				mov al, byte ptr[esi + 3 + ecx * 4]
				cmp ax, 0
				je NEWHOP
				mov resulti, eax

CN://getting the index

			xor eax, eax
				mov ah, byte ptr[esi + ecx*4]
				mov al, byte ptr[esi + ecx*4 + 1]



NEXT : //Encyrpting the data with the hash
				xor ecx, ecx
				mov cl, byte ptr[edx]
				xor cl, byte ptr[edi + eax]
				mov byte ptr[edx], cl

				inc edx
				cmp edx, ebx   //checks to see if we're done encrypting data
				je NOWDONE

				add eax, resulti  //update by the hop count
				cmp eax, 65537  //check boundary conditions
				jb NEXT//repeat until done 
				sub eax, 65537
				jmp NEXT

NOWDONE :  //checking the number of rounds thats left to determine continuation 
			mov ecx, gNumRounds
				dec ecx
				mov gNumRounds, ecx
				cmp ecx, -1
				jne START2
				mov resulti, 0
	}

	

	return resulti;
} // decryptData

