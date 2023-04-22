/**
 * FantasyUnlimited API
 * Fantasy Unlimited - REST API Description
 *
 * The version of the OpenAPI document: v0.0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { NPC } from './nPC';
import { TravelConnection } from './travelConnection';
import { HostileNPC } from './hostileNPC';


export interface Location { 
    id?: string;
    name?: string;
    description?: string;
    iconName?: string;
    marketAccess?: boolean;
    globalMarketAccess?: boolean;
    bannerImage?: string;
    allowedSecondarySkills?: { [key: string]: number; };
    connections?: Array<TravelConnection>;
    npcIds?: Array<string>;
    hostileNPCIds?: Array<string>;
    minimumLevel?: number;
    maximumLevel?: number;
    npcs?: Array<NPC>;
    hostileNPCs?: Array<HostileNPC>;
}
